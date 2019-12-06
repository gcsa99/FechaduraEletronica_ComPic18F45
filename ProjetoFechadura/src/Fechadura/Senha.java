package Fechadura;
import java.util.Scanner;
import Serial.SerialRxTx;


public class Senha {
	Scanner in = new Scanner(System.in);
	SerialRxTx serial  = null;
	Fechadura fechadura = null;
	private int max = 8;
	private String admin= "00000000";
	private String password;
	private int contador;
	int aux1;
	
	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getAdmin() {
		return admin;
	}
	
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean newPassword(String newKey) {
	
		
		System.out.println("\nDigite sua nova senha:");
			String aux = in.nextLine();
			
			if(aux.length() <= max && aux.length() >=4) {
				this.password= aux;
				System.out.println("Senha alterada");

				return true;
			}
			else {
				aux = "";
				System.out.println("\nDigite apenas 8 caraceteres");
				aux1++;
				if(aux1 <=3) {
					newPassword(newKey);
				}
				else {
					System.out.println("\nNão foi possivel alterar sua senha");
					aux1 = 0;
					return false;
				}
			}
		return false;
	}
	
	public void alarme() {
		
		if(contador >= 3) {
			System.out.println("Você errou três vezes, por favor digite a senha padrão para desativar o alarme: ");
			String aux = in.nextLine();
			
			if(aux.equals(admin)) {
				contador = 0;
				System.out.println("Alarme desativado");
			}
			else alarme();	
		}
		contador =0;
	}
	
}

