package com.data_management;

import java.io.IOException;

public class MockDataReader implements DataReader{

    public void readData(DataStorage dataStorage) throws IOException{
        
        dataStorage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        dataStorage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

    }

}
