#include <xc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <pic18f4520.h>
#include "configura.h"
#include "lcd.h"

#define _XTAL_FREQ 4000000              //define frequ�ncia em 4Mhz
#define botao PORTCbits.RC0             //botao que define a porta fechada
#define ledVermelho PORTBbits.RB7       //led que simboliza porta Aberta
#define ledAmarelo PORTBbits.RB5        //led que simboliza porta Fechada
#define ledVerde PORTBbits.RB0          //led que simboliza troca de senha

int flag =0;        //flag para parar while de trocar senha
int flag1 = 0;      //flag para definir se a porta esta aberta ou fechada

void abrirPorta(){
    ledAmarelo =0;
    ledVermelho =0;
    ledVerde =1;        //simboliza porta Aberta
    lcd_clear();        //limpa lcd
    lcd_puts("Porta Aberta");       //escreve no lcd
    flag1 = 1;          //flag para mostrar porta aberta
}

void trocarSenha(){
    lcd_clear();        //limpa lcd
    lcd_puts("Modo mudar senha:");      //escreve no lcd
    lcd_goto(40);       //escreve na segunda linha do lcd
    lcd_puts("Digite sua nova senha:");     //escreve no lcd
    while(flag !=1){        //condi��o para piscar led indicando mudan�a de senha
        ledAmarelo =1;      //liga led para indicar mudan�a de senha
        __delay_ms(100);    
        ledAmarelo =0;      //desliga led 
        __delay_ms(100);
        
        if(RCREG == 'c'){           //verifica se o dado na serial � para parar a mudan�a de senha
            flag =1;                //define flag para parar while
            lcd_clear();            
            lcd_puts("Senha alterada");     //escreve no lcd
            __delay_ms(1000);           //espera 1 seg para limpar lcd
            
            if(flag1 == 1){
                lcd_clear();
                lcd_puts("Porta aberta");
            }
    
            else{
                lcd_clear();
                lcd_puts("Porta fechada");
            }
        }
        else {
            if(RCREG == 'd') {      //verifica se o dado enviado pela serial � para n�o atualizar senha
                flag =1;            //define flag para parar while
                lcd_clear();
                lcd_puts("Senha n�o alterada");     //escreve no lcd
                __delay_ms(1000);       //espera 1 seg para limpar lcd
                lcd_clear();            //limpa lcd
                lcd_puts("Digite sua senha:");          //escreve o lcd
            }
        }
    }
    flag =0;        //limpa vari�vel para pr�xima vez que entrar na fun��o
}

void senhaAlterada(){           //fun��o que altera senha
    lcd_clear();                //limpa lcd
    lcd_puts("Senha alterada com sucesso:");   //escreve no lcd
    
    if(flag1 == 1){     //verifica se a porta esta aberta
        lcd_clear();
        lcd_puts("Porta aberta");       //escreve no lcd
    }
    
    else{               //se a porta estiver fechada
        lcd_clear();
        lcd_puts("Porta fechada");          //escreve no lcd
    }
}

void alarme() {             //fun��o que liga o alarme
    ledVermelho =1;         //ativa led
    ledAmarelo =1;          // ativa led
    ledVerde =1;            // ativa led
    lcd_clear();            // limpa lcd
    lcd_puts("    ALARME");     //escreve no lcd
    lcd_goto(40);           //escreve na segunda linha do lcd
    lcd_puts("Digite a senha padrao:");         //escreve no lcd
}

void desligAlarme(){        //fun��o que desliga alarme
    ledAmarelo =0;          //deslig led
    ledVerde =0;            // deslig led
    ledVerde =0;            //deslig led
    lcd_clear();            //limpa lcd
    lcd_puts("Alarme Desligado");       //escreve no lcd
    __delay_ms(1000);       //esperar 1 seg para limpar lcd
    lcd_clear();            //limpa lcd
    lcd_puts("Digite sua senha");           //escreve no lcd
}

void main(void) {
    config();       //inicia a configura��o
    lcd_init();     //inicia o lcd
    PORTD = 0;      
    PORTB = 0;     
    PORTE = 0;
    ledVermelho =1;     //liga led para simbolizar porta fechada
    lcd_clear();        //limpa lcd
    lcd_puts("Digite sua senha:");      //escreve no lcd
    
    while(1){                   //loop principal
        
        if(!botao){                 //verifica se o botao foi apertado
            __delay_ms(5);
            if(!botao){
                while(!botao){}
                lcd_clear();        //limpa lcd
                lcd_puts("Digite sua senha: ");     //escreve no lcd
                ledVermelho = 1;        //liga led para simbolizar porta fechada
                ledVerde = 0;
                ledAmarelo =0;
                flag1 = 0;      //define flag para mostrar porta fechada
            }
        }
    }
}

void __interrupt(high_priority) tmr (void)      //fun��o de interrup��o por comunica��o
{
    if (RCIF)       //se tiver dado chegando pela serial
    {
        RCIF=0;     
        
        if(RCREG == 'a'){       //verifica se o dado � para abrir porta
            abrirPorta();       //chama fun��o para abrir porta
        }
        
        else {
            if(RCREG == 'b'){       //verifica se o dado � para modo mudar senha
                trocarSenha();      //chama fun��o para trocar senha
            }
            else {
                    if(RCREG == 'e'){       //verifica se o dado � para ativar alarme
                        alarme();       //chama fun��o para ligar alarme
                    }
                    else if(RCREG == 'f'){      //verifica se o dado � para desligar alarme
                        desligAlarme();         //chama fun��o para desligar alarme
                    }
            }
        }
    }
}    