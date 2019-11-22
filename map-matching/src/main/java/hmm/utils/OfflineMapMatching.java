package hmm.utils;

import com.bmw.hmm.SequenceState;
import com.bmw.hmm.ViterbiAlgorithm;
import hmm.types.*;

import java.util.*;

import static hmm.utils.Helper.computeDistance;


public class OfflineMapMatching {

    private final HmmProbabilities hmmProbabilities = new HmmProbabilities();

    private List<GpsMeasurement> gpsMeasurements;

    private RoadEdgeIndex roadEdgeIndex = new RoadEdgeIndex();

    private RoadNetwork roadNetwork;

    private double searchRadius;

    /**
     *
     * @param gpsMeasurements   GPS轨迹数据点集合
     * @param roadEdges         路网数据
     * @param searchRadius      搜索半径(m)
     */
    public OfflineMapMatching(List<GpsMeasurement> gpsMeasurements,
                              List<RoadEdge> roadEdges, double searchRadius) {
        this.gpsMeasurements = gpsMeasurements;
        for (RoadEdge roadEdge : roadEdges) {
            this.roadEdgeIndex.add(roadEdge);
        }
        this.roadNetwork = new RoadNetwork(roadEdges);
        this.searchRadius = searchRadius;
//        roadEdgeIndex.tree.visualize(600,600).save("target/mytree.png");
    }

    /**
     * Returns map matching candidates.
     */
    private Collection<RoadPosition> computeCandidates(GpsMeasurement gpsMeasurement) {
        Collection result = roadEdgeIndex.search(gpsMeasurement, searchRadius);
        return result;
    }

    /**
     * Returns the shortest route length between two road positions.
     */
    private double computeRouteLength(RoadPosition from, RoadPosition to) {
        return roadNetwork.computePathDistance(from, to);
    }

    private void computeEmissionProbabilities(
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep) {
        for (RoadPosition candidate : timeStep.candidates) {
            final double distance =
                    computeDistance(candidate.position, timeStep.observation.position);
            timeStep.addEmissionLogProbability(candidate,
                    hmmProbabilities.emissionLogProbability(distance));
        }
    }

    private void computeTransitionProbabilities(
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> prevTimeStep,
            TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep) {
        final double linearDistance = computeDistance(prevTimeStep.observation.position,
                timeStep.observation.position);
        final double timeDiff = (timeStep.observation.time.getTime() -
                prevTimeStep.observation.time.getTime()) / 1000.0;

        for (RoadPosition from : prevTimeStep.candidates) {
            for (RoadPosition to : timeStep.candidates) {

                // For real map matching applications, route lengths and road paths would be
                // computed using a router. The most efficient way is to use a single-source
                // multi-target router.
                final double routeLength = computeRouteLength(from, to);
                timeStep.addRoadPath(from, to, new RoadPath(from, to));

                final double transitionLogProbability = hmmProbabilities.transitionLogProbability(
                        routeLength, linearDistance, timeDiff);
                timeStep.addTransitionLogProbability(from, to, transitionLogProbability);
            }
        }
    }

    public List<SequenceState<RoadPosition, GpsMeasurement, RoadPath>> run() {
        ViterbiAlgorithm<RoadPosition, GpsMeasurement, RoadPath> viterbi =
                new ViterbiAlgorithm<>();
        TimeStep<RoadPosition, GpsMeasurement, RoadPath> prevTimeStep = null;

        for (GpsMeasurement gpsMeasurement : gpsMeasurements) {
            final Collection<RoadPosition> candidates = computeCandidates(gpsMeasurement);
            final TimeStep<RoadPosition, GpsMeasurement, RoadPath> timeStep =
                    new TimeStep<>(gpsMeasurement, candidates);

            computeEmissionProbabilities(timeStep);
            if (prevTimeStep == null) {
                viterbi.startWithInitialObservation(timeStep.observation, timeStep.candidates,
                        timeStep.emissionLogProbabilities);
            } else {
                computeTransitionProbabilities(prevTimeStep, timeStep);
                viterbi.nextStep(timeStep.observation, timeStep.candidates,
                        timeStep.emissionLogProbabilities, timeStep.transitionLogProbabilities,
                        timeStep.roadPaths);
            }
            prevTimeStep = timeStep;
        }

        List<SequenceState<RoadPosition, GpsMeasurement, RoadPath>> roadPositions =
                viterbi.computeMostLikelySequence();

        return roadPositions;
    }

}

