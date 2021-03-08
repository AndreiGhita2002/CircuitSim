package circuit;

public class Lamp extends Component {

    //TODO make Lamp work properly
    @Override
    Double getBrightness() {
        if (current != 0) {
            return 1.0;
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
