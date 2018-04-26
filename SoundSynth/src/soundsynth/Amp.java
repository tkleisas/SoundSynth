/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundsynth;

/**
 *
 * @author tkleisas
 */
public class Amp {
    public Amp()
    {
        
    }
    public float next(float signal, float gain)
    {
        return signal * gain;
    }
}
