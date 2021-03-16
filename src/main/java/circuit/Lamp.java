package circuit;

public class Lamp extends Component {

    @Override
    Double getBrightness() {
        if ((int)(current * 10) != 0 && Circuit.maxCurrent > 0) {
            return Circuit.maxCurrent / current;
        }
        return 0.0;
    }

    @Override
    String getType() {
        return "Lamp";
    }

    Lamp(Double resistance) {
        super();
        this.resistance = resistance;
    }
}
