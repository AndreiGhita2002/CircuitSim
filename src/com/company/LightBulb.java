package com.company;

public class LightBulb extends Component {

    @Override
    Double getBrightness() {
        if (current != 0) {
            return 1.0;
        }
        return 0.0;
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
