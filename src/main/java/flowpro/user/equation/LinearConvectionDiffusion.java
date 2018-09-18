/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowpro.user.equation;

import flowpro.api.ElementData;
import flowpro.api.Equation;
import flowpro.api.FlowProProperties;
import java.io.IOException;
import java.util.Arrays;

public class LinearConvectionDiffusion implements Equation {

    protected class BoundaryType {

        static final int WALL = -1;
        static final int INLET = -2;
        static final int OUTLET = -3;
        static final int INVISCID_WALL = -4;
    }

    int dim;
    int nEqs;
    boolean isDiffusive;

    public double[] a;
    public double k;
    public double Win;

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
        return true;
    }

    @Override
    public boolean isDiffusive() {
        return isDiffusive;
    }

    @Override
    public void init(FlowProProperties props) throws IOException {

        dim = props.getInt("dimension");
        nEqs = 1;

        // inlet
        if (props.containsKey("a")) {
            a = props.getDoubleArray("a");
            if (a.length != dim) {
                throw new IOException("length of a must be same as dimension");
            }
        } else {
            throw new IOException("vector a must be specifed");
        }

        isDiffusive = false;
        if (props.containsKey("k")) {
            k = props.getDouble("k");
            isDiffusive = true;
        }
        
        if (props.containsKey("Uin")) {
            Win = props.getDouble("Uin");
        }

    }

    @Override
    public void setState(double dt, double t) {  
    }
    
    @Override
    public double[] constInitCondition() {
        return new double[]{Win};
    }

    @Override
    public void limitUnphysicalValues(double[] Ws, double[] W, int nBasis) { // limituje zaporne hodnoty
    }

    //  nevazky tok stenou _____________________________________________________
    @Override
    public double[] numericalConvectiveFlux(double WL[], double WR[], double[] n, int TT, ElementData elem) {
        limite(WL);
        limite(WR);

        double[] f = new double[nEqs];

        double[] fL = convectiveFlux(WL, n, elem);
        double[] fR = convectiveFlux(WR, n, elem);
        double maxEigenValue = maxEigenvalue(WL, elem);
        for (int j = 0; j < nEqs; j++) {
            f[j] = (fL[j] + fR[j]) / 2 - maxEigenValue * (WR[j] - WL[j]) / 2;
        }
        return f;
    }

    @Override
    public double[] convectiveFlux(double[] W, double[] n, ElementData elem) {
        double[] f = new double[nEqs];
        double Vn = 0;
        for(int d = 0; d < dim; d++){
            Vn += n[d]*a[d];
        }
        f[0] = Vn*W[0];
        return f;
    }

    @Override
    public double[] diffusiveFlux(double[] W, double[] dW, double[] n, ElementData elem) {
        double[] f = new double[nEqs];
        for(int d = 0; d < dim; d++){
            f[0] += k*dW[d]*n[d];
        }
        return f;
    }

    @Override
    public double[] numericalDiffusiveFlux(double Wc[], double dWc[], double[] n, int TT, ElementData elem) {
        return diffusiveFlux(Wc, dWc, n, elem);
    }

    @Override
    public boolean isSourcePresent() {
        return false;
    }

    @Override
    public double[] sourceTerm(double[] W, double[] dW, ElementData elem) { // zdrojovy clen
        throw new UnsupportedOperationException("source is not present");
    }

    @Override
    public double[] boundaryValue(double[] WL, double[] n, int TT, ElementData elem) {
        double[] WR = new double[nEqs];
        switch (TT) {
            case (BoundaryType.WALL):
                WR = Arrays.copyOf(WL, nEqs);
                break;
            case (BoundaryType.INVISCID_WALL):
                WR = Arrays.copyOf(WL, nEqs);
                break;

            case (BoundaryType.INLET):
                WR[0] = Win;
                break;

            case (BoundaryType.OUTLET):
                WR = Arrays.copyOf(WL, nEqs);
        }
        return WR;
    }

    @Override
    public double pressure(double[] W) {
        return 0;
    }

    @Override
    public double maxEigenvalue(double[] W, ElementData elem) {
        double max = a[0];
        for (int d = 1; d < dim; d++) {
            if (a[d] > max) {
                max = a[d];
            }
        }
        return max;
    }

    void limite(double[] W) {
    }

    @Override
    public boolean isIPFace(int TT) {
        return false;
    }

    @Override
    public void saveReferenceValues(String filePath) throws IOException {
    }

    @Override
    public double[] getReferenceValues() {
        return new double[]{1};
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
