package com.audaharvest.iservices;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.audaharvest.model.Vehicle;

public interface IVehicleService {
	public Vehicle getVehicleDetails();
}
