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
public class LowPassFilter {
    
    float q;
    float f;
    float fb;
    //float cutoff;
    //float resonance;
    float buf0;
    float buf1;
    public LowPassFilter(){
    }


	/** Set the cut off frequency,
	@param cutoff use the range 0-255 to represent 0-8192 Hz (AUDIO_RATE/2).
	Be careful of distortion at the lower end, especially with high resonance.
	*/
    public void setCutoffFreq(float cutoff)
	{
		f = cutoff;
		//fb = q+ucfxmul(q, SHIFTED_1 - cutoff);
                fb = q + (q*(1-cutoff));
	}


	/** Set the resonance.  If you hear unwanted distortion, back off the resonance.
	@param resonance in the range 0-255.
	*/
    public void setResonance(float resonance)
	{
		q = resonance;
	}

	/** Calculate the next sample, given an input signal.
	@param in the signal input.
	@return the signal output.
	@note Timing: about 11us.
	*/
	//	10.5 to 12.5 us, mostly 10.5 us (was 14us)
	
        float next(float in)
                
	{
		//setPin13High();
		buf0 = buf0+(((in - buf0)+(fb*buf0-buf1)) * f );
                //buf0+=fxmul(((in - buf0) + fxmul(fb, buf0-buf1)), f);
		buf1 = buf1 + ((buf0-buf1) * f);
                //buf1+=ifxmul(buf0-buf1, f); // could overflow if input changes fast
		//setPin13Low();
		return buf1;
	}




}
	
