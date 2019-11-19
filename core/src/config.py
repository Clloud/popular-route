import math


class Config_1:
    DATASET_ROOT_DIR = '../../data/most-popular-route-test/0/Data'  # The data set root directory
    DATASET_SCALE    = 0                # How many users' trajectory data are choosed
    TRAJACTORY_SCALE = 20               # How many trajectories are choosed per user
    RANGE = {                           # To pick trajectory points within the range
        'status': False
    }

    GROUP_SIZE_THRESHOLD     = 3        # group size threshold φ
    COHERENCE_THRESHOLD      = 0.4      # coherence threshold τ
    SCALING_FACTOR           = 1.5      # scaling factor δ
    TURNING_ALPHA            = 5        # tuning parameter α
    TURNING_BETA             = 2        # tuning parameter β

    RADIUS = SCALING_FACTOR * \
        ((-math.log(COHERENCE_THRESHOLD)) ** (1 / TURNING_ALPHA))


class Config_2:
    DATASET_ROOT_DIR = '../../data/most-popular-route-test/1/Data'  # The data set root directory
    DATASET_SCALE    = 0                # How many users' trajectory data are choosed
    TRAJACTORY_SCALE = 20               # How many trajectories are choosed per user
    RANGE = {                           # To pick trajectory points within the range
        'status': False
    }

    GROUP_SIZE_THRESHOLD    = 3         # group size threshold φ
    COHERENCE_THRESHOLD     = 0.49      # coherence threshold τ
    SCALING_FACTOR          = 1.1       # scaling factor δ
    TURNING_ALPHA           = 5         # tuning parameter α
    TURNING_BETA            = 2         # tuning parameter β

    RADIUS = SCALING_FACTOR * \
        ((-math.log(COHERENCE_THRESHOLD)) ** (1 / TURNING_ALPHA))


class Config(Config_2):
    __attr__ = ['DATASET_ROOT_DIR', 'DATASET_SCALE', 'TRAJACTORY_SCALE', 'RANGE',
        'GROUP_SIZE_THRESHOLD', 'COHERENCE_THRESHOLD', 'SCALING_FACTOR', 
        'TURNING_ALPHA', 'TURNING_BETA', 'RADIUS']

    def __str__(self):
        s = ""
        for attr in self.__attr__:
            s += attr + ' ' + str(getattr(self, attr)) + '\n'
        return s

    def __repr__(self):
        return self.__str__()