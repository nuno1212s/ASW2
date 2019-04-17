package rsa.service;

import rsa.shared.RideMatchInfo;

import java.util.Comparator;

public interface RideMatchInfoSorter {

    Comparator<RideMatchInfo> getComparator();

}
