package com.audaharvest.services.validators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.audaharvest.iservices.IValidatorService;
import com.audaharvest.model.Vehicle;

public class StockValidator implements IValidatorService{

	@Override
	public boolean validate(Future<Vehicle> list) {
		boolean isValid = true;
		try {
			if (list.get().getStockNumber()!= null) {
				String str = list.get().getStockNumber().trim();
				String[] strArray = str.trim().split(" ");
				if(strArray.length > 2)
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
