#!/usr/bin/python
# -*- coding: utf-8 -*-

import matplotlib.pyplot as plt
import os

root_path = 'C:\\Users\\刘晓璇\\Desktop\\popular-route\\data\\'


# 读取trajectory(.plt)，返回包含纬度和经度的列表
def get_trajectory(filename):
    fopen = open(filename, 'r')
    latitude = []
    longitude = []
    count = 0
    for eachline in fopen:
        count += 1
        if count > 6:
            group = eachline.split(",")
            latitude.append(float(group[0]))
            longitude.append(float(group[1]))
    fopen.close()
    return (latitude, longitude)


# 读取trajectory_map_matched(.txt)，返回包含纬度、经度和edge_id的列表
def get_popular_route(filename):
    fopen = open(filename, 'r')
    latitude = []
    longitude = []
    edge_id = set()
    count = 0
    for eachline in fopen:
        count += 1
        if count > 1:
            group = eachline.split("\t")
            latitude.append(float(group[4]))
            longitude.append(float(group[3]))
            edge_id.add(int(group[0]))
    fopen.close()
    return ((latitude, longitude), edge_id)


# 读取road_natwork(.txt)，返回包含纬度和经度的列表
def get_network_route(edge_id):
    fopen = open(root_path + 'map-data\\road_network_beijing.txt', 'r')
    latitude = []
    longitude = []
    count = 0
    for eachline in fopen:
        count += 1
        if count > 1:
            group = eachline.split("\t")
            if edge_id < int(group[0]):
                continue
            elif edge_id == int(group[0]):
                line = (group[6][11:-2:]).split(",")
                for i in range(len(line)):
                    point = line[i].split(" ")
                    latitude.append(float(point[1]))
                    longitude.append(float(point[0]))
    fopen.close()
    return (latitude, longitude)


def draw_popular_route(filename_trajectory, filename_popular_route):
    plt.ylim(39.984, 39.985)  # 纵轴（纬度）的最小值和最大值
    plt.xlim(116.295, 116.325)  # 横轴（经度）的最小值和最大值
    (latitude1, longitude1) = get_trajectory(filename_trajectory)
    ((latitude2, longitude2), edge_id) = get_popular_route(filename_popular_route)
    for id in edge_id:
        (latitude3, longitude3) = get_network_route(int(id))
        plt.scatter(longitude3, latitude3, 5, 'g')
        plt.plot(longitude3, latitude3, 'g-', linewidth=4)
    plt.scatter(longitude1, latitude1, 3, 'b')
    plt.scatter(longitude2, latitude2, 5, 'r')
    # plt.plot(longitude1, latitude1, linewidth=1)
    plt.plot(longitude2, latitude2, 'r-', linewidth=3)
    plt.show()


draw_popular_route(root_path + 'trajectory\\000\\Trajectory\\20081023025304.plt',
                   root_path + 'trajectory-map-matched\\20081023025304.txt')
