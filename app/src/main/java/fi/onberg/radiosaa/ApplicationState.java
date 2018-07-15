package fi.onberg.radiosaa;

import java.io.Serializable;

public class ApplicationState implements Serializable {
    private double temperature;
    private double connection;

    private ApplicationState(){
        // This class uses the builder pattern
    }

    public double getTemperature(){
        return temperature;
    }

    public double getConnection(){
        return connection;
    }

    @Override
    public String toString() {
        return "ApplicationState{" +
                "temperature=" + temperature +
                ", connection=" + connection +
                '}';
    }

    public static class Builder {
        private ApplicationState state;

        public Builder(ApplicationState copy){
            state = new ApplicationState();
            if(copy != null){
                state.temperature = copy.temperature;
                state.connection = copy.connection;
            } else {
                state.temperature = 0;
                state.connection = 0;
            }
        }

        public Builder setTemperature(double pressure){
            state.temperature = pressure;
            return this;
        }

        public Builder setConnection(double connection){
            state.connection = connection;
            return this;
        }

        public ApplicationState build(){
            return state;
        }
    }
 }
