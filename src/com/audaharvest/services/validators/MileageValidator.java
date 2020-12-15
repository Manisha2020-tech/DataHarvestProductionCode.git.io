package com.audaharvest.services.validators;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.audaharvest.iservices.IValidatorService;
import com.audaharvest.model.Vehicle;

public class MileageValidator implements IValidatorService{

	@Override
	public boolean validate(Future<Vehicle> list) {
		boolean isValid = true;
		try {
			if (list.get().getMileage()!= null) {
				String mileageVal = list.get().getMileage().replaceAll("\\D+", "").trim();				
				//if(mileageVal.equals("0") || (mileageVal.length() > 8))
				if(mileageVal.length() > 8)
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
