package flowpro.user.equation;

import flowpro.api.ElementData;
import flowpro.api.Equation;
import flowpro.api.FlowProProperties;
import java.io.IOException;

public class WaveEquation implements Equation {

    double c; // characteristic speed
    double[] WIn;

    int dim;
    int nEqs;
    boolean isDiffusive;
    
    @Override
    public int dim() {
        return dim;
    }

    @Override
    public int nEqs() {
        return nEqs;
    }

    @Override
    public boolean isConvective() {
        return false;
    }

    @Override
    public boolean isDiffusive() {
        return true;
    }
    
    @Override
    public boolean isSourcePresent() {
        return false;
    }
    
    @Override
    public void init(FlowProProperties props) throws IOException {
        // initial condition
        c = props.getDouble("c");
        
        dim = props.getInt("dimension");
        nEqs = 1;
        WIn = new double[nEqs];
        WIn[0] = 0;
    }

    @Override
    public double[] constInitCondition() {
        return WIn;
    }

    //  nevazky tok stenou _____________________________________________________
    @Override
    public double[] numericalConvectiveFlux(double WL[], double WR[], double[] n, int TT, ElementData elem) {
        throw new UnsupportedOperationException("numericalConvectiveFlux is not present");
    }

    @Override
    public double[] convectiveFlux(double[] W, double[] n, ElementData elem) {
        throw new UnsupportedOperationException("convectiveFlux is not present");
    }

    @Override
    public double[] sourceTerm(double[] W, double[] dW, ElementData elem) { // zdrojovy clen
        throw new UnsupportedOperationException("sourceTerm is not present");
    }

    @Override
    public double[] boundaryValue(double[] WL, double[] n, int TT, ElementData elem) {
        double[] WR = new double[nEqs];
        double t = elem.currentT;
        switch (TT) {
            case (-1): // stena
                WR[0] = 0;
                break;
            case (-2):
                double omega = 10;
                WR[0] = Math.sin(omega * t);
                break;

        }
        return WR;
    }

    @Override
    public double[] numericalDiffusiveFlux(double Wc[], double dWc[], double[] n, int TT, ElementData elem) {
        return diffusiveFlux(Wc, dWc, n, elem);
    }

    @Override
    public double[] diffusiveFlux(double[] W, double[] dW, double[] n, ElementData elem) {
        double[] fv = new double[nEqs];
        double dWn = 0;
        for(int d = 0; d < dim; d++){
            dWn += dW[d] * n[d];
        }
        fv[0] = c * dWn;
        return fv;
    }

    @Override
    public double pressure(double[] W) {
        return 0;
    }

    @Override
    public double maxEigenvalue(double[] W, ElementData elem) {
        return c;
    }

    @Override
    public boolean isIPFace(int TT) {
        return TT == -1 || TT == -2;
    }

    @Override
    public void limitUnphysicalValues(double[] Ws, double[] W, int nBasis) { // limituje zaporne hodnoty
    }

    @Override
    public void saveReferenceValues(String filePath) throws IOException {
        //MyProperties output = new MyProperties();
        //output.setProperty("T", Double.toString(TRef));
        //output.store(new FileOutputStream(filePath), null);
    }
    
    @Override
    public double[] getReferenceValues(){
        return null;
    }
    
    @Override
    public double[] getResults(double[] W, double[] X, String name) {
        switch (name) {
            case "u":
                return new double[]{W[0]};

            default:
                throw new UnsupportedOperationException("undefined value " + name);
        }
    }
    
    @Override
    public boolean isEquationsJacobian(){
        return false;
    }
    
    @Override
    public double[] convectiveFluxJacobian(double[] W, double[] n, ElementData elemData){
        throw new UnsupportedOperationException("operation not supported");
    }
    
    @Override
    public double[] diffusiveFluxJacobian(double[] W, double[] dW, double n[], ElementData elemData){
        throw new UnsupportedOperationException("operation not supported");
    }
    
    @Override
    public double[] sourceTermJacobian(double[] W, double[] dW, ElementData elemData){
        throw new UnsupportedOperationException("operation not supported");
    }
}