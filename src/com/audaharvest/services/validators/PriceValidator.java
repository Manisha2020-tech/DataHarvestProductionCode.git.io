package com.audaharvest.services.validators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.audaharvest.iservices.IValidatorService;
import com.audaharvest.model.Vehicle;

public class PriceValidator implements IValidatorService{

	@Override
	public boolean validate(Future<Vehicle> list) {
		boolean isValid = true;
			try {
				if (list.get().getPrice()!= null) {
					int priceLength = list.get().getPrice().replaceAll("\\D+", "").trim().length();
					if(priceLength > 8)
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
