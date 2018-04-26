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
public class SoundSynth implements AudioCreation.NjsCallback{
    volatile long stime = 0;
    int sr = 44100;
    int numchannels = 2;
    float maxfreq = 440;
    float minfreq = 22.5f;
    float freq1=maxfreq, freq2 = minfreq;
    LowPassFilter filter;
    ADSR adsr = null;
    ADSR d_adsr = null;
    Amp myamp = null;
    float tempo = 120.0f;
    float beatlength;
    float mastertune = 440.0f;
    int[] scale = {0,3,5,7,10};
    int[] melody = {0,0,2,1,0,0,2,1,0,0,3,2,0,4,0,3,0,2,0,1};
    float[] notes;
    int currnote = 0;
    int quarternote =0;
    public SoundSynth()
    {
        filter = new LowPassFilter();
        filter.setResonance(0.9f);
        filter.setCutoffFreq(1.0f);
        adsr = new ADSR(sr);
        adsr.setAttack(100f);
        adsr.setDecay(100f);
        adsr.setSustain(0.7f);
        adsr.setRelease(100f);
        d_adsr = new ADSR(sr);
        d_adsr.setAttack(30f);
        d_adsr.setDecay(10f);
        d_adsr.setSustain(1.0f);
        d_adsr.setRelease(20f);
        myamp = new Amp();
        beatlength = (60f * sr) / tempo;
        System.out.println("beatlength="+beatlength);
        notes = new float[128];
        retune(mastertune);
        quarternote = (int)(beatlength /4);
    }
    public void retune(float masterfreq)
    {
        mastertune = masterfreq;
        for(int i = 0;i<notes.length;i++)
        {
            notes[i] = (float)Math.pow(2, (i-69.0)/12.0) * mastertune;
        }
    }
    public float mtof(int note)
    {
        return notes[note];
    }
    public float saw(float freq, float t)
    {
        return (float)  (t*freq - (float)Math.floor(0.5f + t*freq));
        
    }
    public float square(float freq, float t)
    {
        return (float)Math.signum(Math.sin(2*Math.PI*freq*t));
    }
    public float noise()
    {
        return (float)Math.random();
    }
    public float getNextNote()
    {
        float freq = mtof(scale[melody[currnote++%melody.length]%scale.length]+30);
        //System.out.println("note freq="+freq);
        return freq;
    }
    public void render(float[] output, int nframes)
    {
        if(freq1<minfreq)
        {
            freq1 = maxfreq;
        }
        if(freq2>maxfreq)
        {
            freq2 = minfreq;
        }
        for(int i = 0;i<nframes*numchannels;i = i + 2)
        {
            /*
            if(stime%100>50)
            {
                output[i] = 1.0f;
            }
            else
            {
                output[i] = -1.0f;
            }*/
            //output[i] = (float)Math.sin(2*Math.PI*220*(stime/(float)sr)); 
            //output[i+1] = (float)Math.sin(2*Math.PI*220*(stime/(float)sr)); 
            output[i] =// filter.next(
                    myamp.next(saw(freq1,(float)stime/(float)sr)+square(freq1,(float)stime/(float)sr), adsr.next());
                    
            //);
            //output[i+1] = output[i] ;//saw(freq2,(float)stime/(float)sr);
            output[i+1] = 
                    myamp.next(noise(), d_adsr.next());
            
            if(stime%quarternote==0)
            {
                
                freq1 = getNextNote();
                adsr.trigger();
                d_adsr.trigger();
                d_adsr.release();
                //System.out.println(output[i] +":"+ stime);
                
            }
            if(stime+quarternote%quarternote==0)
            {
                if(adsr.isTriggered())
                {
                    adsr.release();
                }
                
            }

            stime++;

        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Thread t = new Thread()
        {
            public void run()
            {
                AudioCreation a = new AudioCreation();
                 a.callback = new SoundSynth();
                 a.open(44100, 2, 40);
                 a.start();
                 
                       
            }
        };
        t.start();
        while(true)
        {
            try
            {
                java.lang.Thread.sleep(100);
            }
            catch(Exception e)
            {
                
            }
        }
        
    }
    
}
