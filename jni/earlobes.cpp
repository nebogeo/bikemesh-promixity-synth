#include "earlobes.h"
#include "Modules.h"
#include <math.h>

short state;
Sample *in;
Sample *out;
unsigned int size;

MoogFilter moog(16000);
WaveTable lfo(16000);
Sample lfo_s;
Sample lfo2_s;
Sample lfo3_s;

WaveTable osc(16000);
Sample osc_s;
WaveTable osc2(16000);
Sample osc2_s;
Sample ring_s;
WaveTable osc3(16000);
Sample osc3_s;

float f1,f2,f3;

void init(unsigned int s)
{
    size=s;
    in = new Sample(size);
    out = new Sample(size);

    f1=f2=f3=0;

    moog.SetCutoff(0.2);
    moog.SetResonance(0.4);
    WaveTable::WriteWaves();
    lfo.SetOctave(0);
    //lfo.Trigger(0,0.1,0.1,1);
    lfo.SetType(WaveTable::REVSAW);
    osc.SetOctave(0);
    osc2.SetOctave(0);
    osc3.SetOctave(0);
	osc2.SetType(WaveTable::NOISE);
    lfo.SetFineFreq(10);

    lfo_s.Allocate(size);
    lfo2_s.Allocate(size);
    lfo3_s.Allocate(size);
    osc_s.Allocate(size);
    osc2_s.Allocate(size);
    osc3_s.Allocate(size);
    ring_s.Allocate(size);

    osc.SetFineFreq(400);
}

float mix=0;
float mesh=0;
float pitch1=0;
float pitch2=0;
float pitch3=0;
float volume=1;

void set_param(int which,int amount)
{
/*    if (which==0) moog.SetCutoff(amount/100.0f);
    else if (which==1) moog.SetResonance(amount/100.0f*0.5);
    else
    {
        mix=(amount-10)/100.0f;
        if (mix<0) mix=0;
    }
*/

    float l=(100-amount)*2;

    if (which==3) {
        lfo.SetFineFreq(l*l*0.00005);
        moog.SetCutoff(l*0.05*0.4);
    }

    if (which==0) pitch1=(amount/100.0f)*200;
    if (which==1) pitch2=(amount/100.0f)*100;
    if (which==2) pitch3=(amount/100.0f)*150;

//    if (which==0) osc.SetFineFreq((amount/100.0f)*10);

//    if (which==1) osc2.SetFineFreq((amount/100.0f)*4);
//    if (which==2) osc3.SetFineFreq((amount/100.0f)*4);

    if (which==4) volume=amount/100.0f;

}

void process(short *data)
{
/*    for (unsigned int i=0; i<size; ++i)
    {
        (*in)[i] = (float)(data[i] / 32767.0f);
    }

//    moog.Process(size,*in,NULL,out,NULL,NULL);
    lfo.Process(size,lfo_s);
    for (unsigned int i=0; i<size; ++i) lfo_s[i]=(lfo_s[i]*0.5+0.5);

    osc.Process(size,osc_s);
    for (unsigned int i=0; i<size; ++i) osc_s[i]=osc_s[i]*4040*lfo_s[i]+440;

    osc2.ProcessFM(size,osc2_s,osc_s);

    for (unsigned int i=0; i<size; ++i) ring_s[i]=osc2_s[i]*(*in)[i];

    if (mix>0) (*in).MulMix(ring_s,mix);

    moog.Process(size,*in,NULL,out,NULL,NULL);

    for (unsigned int i=0; i<size; ++i)
    {
        data[i] = (short)((*out)[i] * 32767.0f);
    }

*/

    lfo.Process(size,lfo_s);
    for (unsigned int i=0; i<size; ++i) lfo2_s[i]=pitch2+(lfo_s[i]*0.5+0.5)*200;
    for (unsigned int i=0; i<size; ++i) lfo3_s[i]=pitch3+(lfo_s[i]*0.5+0.5)*200;
    for (unsigned int i=0; i<size; ++i) lfo_s[i]=pitch1+(lfo_s[i]*0.5+0.5)*200;

    osc.ProcessFM(size,osc_s,lfo_s);
    osc2.ProcessFM(size,osc2_s,lfo2_s);
    osc3.ProcessFM(size,osc3_s,lfo3_s);
//    osc2.Process(size,osc2_s);
//    osc3.Process(size,osc3_s);


    moog.Process(size,osc2_s,NULL,&osc2_s,NULL,NULL);


    for (unsigned int i=0; i<size; ++i)
    {
        data[i] = (short)((osc_s[i]/3+
                           osc2_s[i]/3+
                           osc3_s[i]/3
                              )*32767.0f*volume);
    }


}
