package com.company;

public class Battery extends Component {

    @Override
    String getType() {
        return "Battery";
    }

    Battery(Double emf) {
        super();
        this.PD = emf;
    }
}
