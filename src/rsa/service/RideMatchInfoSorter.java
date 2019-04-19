package rsa.service;

import rsa.shared.RideMatchInfo;

import java.util.Comparator;

/**
 * A type proving a comparator of RideMatchInfo instances.
 * This is part of the abstract component of the Factory Method design pattern.
 */
public interface RideMatchInfoSorter {

    Comparator<RideMatchInfo> getComparator();

}
