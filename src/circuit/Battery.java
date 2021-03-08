package circuit;

public class Battery extends Component {

    @Override
    String getType() {
        return "Battery";
    }

    Battery(Double emf, Double res) {
        super();
        this.PD = emf;
        this.resistance = res;
    }

    Battery(Double emf) {
        this(emf, 0.0);
    }
}
