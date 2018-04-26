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
public class ADSR {
final int STATE_UNTRIGGERED = 0;
final int STATE_ATTACK = 1;
final int STATE_DECAY = 2;
final int STATE_SUSTAIN = 3;
final int STATE_RELEASE = 4;
    float a;
    float d;
    float s;
    float r;
    float sr;
    float acc;
    float timestep;
    int state; 
    boolean triggered = false;
    public ADSR(float sampleRate)
    {
        sr = sampleRate;
        state = STATE_UNTRIGGERED;
        triggered = false;
    }
    void setAttack(float attack) // in milliseconds
    {
        a = attack/1000.0f;
        if(attack<timestep)
        {
            attack = timestep;
        }
    }
    boolean isTriggered()
    {
        return triggered;
    }
    void setDecay(float decay)
    {
        d = decay/1000.0f;
        if(decay<timestep)
        {
            decay = timestep;
        }
    }
    
    void setSustain(float sustain)
    {
        s = sustain;
    }
    void setRelease(float release)
    {
        r = release/1000.0f;
        if(release<timestep)
        {
            release = timestep;
        }
    }
    float next()
    {
        switch(state)
        {
            case STATE_UNTRIGGERED:
                return 0;
            case STATE_ATTACK:
                acc = acc+timestep;
                if(acc<a)
                {
                    
                    return (acc/a);
                }
                else
                {
                    state = STATE_DECAY;
                    float oldacc = acc;
                    acc = 0.0f;
                    return (oldacc/a);
                }
            case STATE_DECAY:
                acc = acc + timestep;
                if (acc<d)
                {
                    return (1-d*acc);
                }
                else
                {
                    state = STATE_SUSTAIN;
                    acc = 0;
                    return s;
                }
            case STATE_SUSTAIN:
            acc = acc+timestep;
            return s;
            case STATE_RELEASE:
                acc = acc+timestep;
                if(acc>r)
                {
                    state = STATE_UNTRIGGERED;
                    triggered = false;
                    return 0;
                }
                return (s-d*acc);
                
            default:
                break;
        }
        return 0;
    }
    public void trigger()
    {
        acc = 0;
        timestep = 1.0f / sr;
        state = STATE_ATTACK;
        triggered = true;
    }
    public void release()
    {
        acc = 0;
        timestep = 1.0f/ sr;
        state = STATE_RELEASE;
    }
}
