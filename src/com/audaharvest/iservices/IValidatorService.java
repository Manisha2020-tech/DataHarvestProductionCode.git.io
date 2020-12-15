package com.audaharvest.iservices;

import java.util.concurrent.Future;

import com.audaharvest.model.Vehicle;

public interface IValidatorService {
	public boolean validate(Future<Vehicle> list);
}
