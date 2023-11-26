package tiqueto.model;

import tiqueto.EjemploTicketMaster;
import tiqueto.IOperacionesWeb;

public class WebCompraConciertos implements IOperacionesWeb {

	private int totalEntradas;
	private int entradasRestantes;
	private final int entradasMaximasFan;
	public static boolean todosFansCompletos = false;
	public static boolean salida = false;
	private String tabuladores = "\t\t";
	public WebCompraConciertos() {
		this.totalEntradas = EjemploTicketMaster.TOTAL_ENTRADAS;
		this.entradasRestantes = 0;
		this.entradasMaximasFan = EjemploTicketMaster.NUM_FANS * EjemploTicketMaster.MAX_ENTRADAS_POR_FAN;
	}

	public synchronized boolean comprarEntrada() {

		if (todosFansCompletos) {
			return false;
		}

		if (entradasRestantes > 0) {
			entradasRestantes--;
			mensajeWeb("Entrada comprada, entradas restantes: " + (entradasRestantes + 1));
			return true;
		}
		else {
				mensajeWeb("No hay entradas disponibles para comprar. Fan en espera.");
		}

		return false;
	}


	public synchronized int reponerEntradas(int numeroEntradas) {

		if (todosFansCompletos) {

			return 0;
		}

			if (totalEntradas > 0) {
				int entradasAReponer = Math.min(numeroEntradas, totalEntradas);
				entradasRestantes += entradasAReponer;
				totalEntradas -= entradasAReponer;
				notifyAll(); // Notificar a todos los fans que hay nuevas entradas
				return entradasAReponer;
			}

		return 0;
	}

	public synchronized void cerrarVenta() {

		if (salida){
			mensajeWeb("Todos los Fans han comprado el maximo de entradas, Venta cerrada.");
			notifyAll();
			if(EjemploTicketMaster.REPOSICION_ENTRADAS > entradasMaximasFan ){
				entradasRestantes = entradasMaximasFan - totalEntradas;
			}else{
				entradasRestantes = totalEntradas; //Si han habido mas entras que compradores , actualizamos las entradas restantes
			}
		}else if (!comprarEntrada()){
			mensajeWeb("Venta cerrada, no quedan entradas disponibles");
			notifyAll(); // Notificar a los fans que la venta ha terminado

		}

	}

	@Override
	public synchronized boolean hayEntradas() {


		return entradasRestantes > 0 ;
	}


	@Override
	public synchronized int entradasRestantes() {

		return entradasRestantes;
	}


	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	private void mensajeWeb(String mensaje) {
		System.out.println(System.currentTimeMillis() + tabuladores +"| WebCompra: " + mensaje);

	}

}