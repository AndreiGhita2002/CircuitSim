package com.company;

public class LightBulb extends Component {

    Double getBrightness() {
        //TODO getBrightness based on the current flowing through
        return 1.0;
    }

    @Override
    String getType() {
        return "LightBulb";
    }

    LightBulb(Double resistance) {
        super();
        this.resistance = resistance;
    }
}
