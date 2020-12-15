package com.audaharvest.services.validators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.audaharvest.iservices.IValidatorService;
import com.audaharvest.model.Vehicle;

public class VinValidator implements IValidatorService{

	@Override
	public boolean validate(Future<Vehicle> list) {
		boolean isValid = true;
		try {
			if (list.get().getVin()!= null) {
				String str = list.get().getVin().replaceAll("(\\s+|\\u00a0)", "");
				if(str.length() > 17)
					isValid = false;
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	
		return isValid;
	}

}
