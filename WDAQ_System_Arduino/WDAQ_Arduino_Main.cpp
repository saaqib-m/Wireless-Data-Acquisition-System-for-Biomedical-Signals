#include <SoftwareSerial.h> 

int i = 0;
int j = 0;
int r_pin = A0;
int sample_output;
int Sfreq = 100;
const uint16_t Timer_count1 = (16000000UL) / (Sfreq*1024UL) - 1;

void setup (){
  noInterrupts();
  Serial.begin (9600);
  ADCSRA |= (1 << ADPS0) |  (1 << ADPS1) | (1 << ADPS2);  // ADC Prescaler of 128

// ------ For Sampling Frequency ------ //
  //set timer1 interrupt at Sfreq Hz
  TCCR1A = 0;// set entire TCCR1A register to 0
  TCCR1B = 0;// same for TCCR1B
  TCNT1  = 0;//initialize counter value to 0
  // set compare match register for 1hz increments
  OCR1A = Timer_count1;// = (16*10^6) / (Sfreq*Timer Prescaler) - 1 (must be <65536)
  // turn on CTC mode
  TCCR1B |= (1 << WGM12);
  
  //(---- Set CS12 and CS11 and CS10 bits for prescaler ----)(Set below Timer 2 for ease)
//  TCCR1B |= (1 << CS12) | (1 << CS10); //-- 1024
//  TCCR1B |= (1 << CS12);               //-- 256
//  TCCR1B |= (1 << CS11) | (1 << CS10); //-- 64
//  TCCR1B |= (1 << CS11);               //-- 8
//  TCCR1B |= (1 << CS10);               //-- 1

  // enable timer COMPARE interrupt
  TIMSK1 |= (1 << OCIE1A);
  
  interrupts();
} 

//// ----- Timer Overflow ----- //
//  TCCR2A = 0;
//  TCCR2B = 0;
//  
//  TCNT2 = 131;                                                   // preload timer (256-16MHz/prescaler/freq)
//  TIMSK2 |= (1 << TOIE2);                                        // enable timer OVERFLOW interrupt- DISABLING THIS STOPS OVF
//
//  interrupts();
//  TCCR2B |= (1 << CS20) | (1 << CS21) | (1 << CS22);             // 1024 prescaler for Timer2

  TCCR1B |= (1 << CS12) | (1 << CS10);                           // 1024 prescaler for Timer1

  interrupts();
} 

ISR(TIMER1_COMPA_vect){
  sample_output = analogRead (r_pin);
  Serial.print(sample_output);  
  Serial.print(' ');
  Serial.print(Sfreq);
  Serial.print(' ');
  Serial.println(i);
  i++;
}
//ISR(TIMER2_OVF_vect) {
//  TCNT2 = 131;
//  j++;
////  TCCR1B &= ~(1 << CS12);
////  TCCR1B &= ~(1 << CS10);
//
////  Serial.println(TCCR1B,BIN);
//}

void loop (){
//  // Testing with the Timer 2 overflow to obtain samples 
//  if (j>125){
//    TCCR1B &= ~(1 << CS12);
//    TCCR1B &= ~(1 << CS10);
//  }
 } 