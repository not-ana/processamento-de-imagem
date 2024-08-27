import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.gui.GenericDialog;

import ij.IJ;
import ij.ImagePlus;

public class Convert_RGB_to_Grayscale_Plugin_AJP implements PlugIn {
	
	public void run(String arg) {
		
		ImagePlus imp = IJ.openImage();
		
		if (imp == null) {
			IJ.error("Erro ao abrir a imagem");
			IJ.showMessage("PlugIn cancelado!");
			return;
		} else if (imp.getType() != ImagePlus.COLOR_RGB) {
			IJ.error("Apenas imagens RGB são suportadas!");
			IJ.showMessage("PlugIn cancelado!");
			return;
		}
		apresentarInterfaceGrafica(imp);
	}
	
	
	
	
	public void apresentarInterfaceGrafica(ImagePlus imp) {
		GenericDialog interfaceGrafica = new GenericDialog("Escolha de Estratégia");
		
		String[] estrategias = {"Média", "TV", "Digital"}; 
		interfaceGrafica.addMessage("Escolha qual estratégia quer usar para converter uma imagem RGB em escala de cinza");
		interfaceGrafica.addRadioButtonGroup("Escolha a estratégia", estrategias, 1, 3, "Digital");
		interfaceGrafica.addCheckbox("Criar nova imagem em tons de cinza", true);
		interfaceGrafica.showDialog();
		
		if (interfaceGrafica.wasCanceled()) {
			IJ.showMessage("PlugIn cancelado!");
			return;
		}
		
		String escolhaRadioButton = interfaceGrafica.getNextRadioButton(); 
		
		switch (escolhaRadioButton) {
			case "Média":
				IJ.log("Executando Estratégia de Média");
				media(imp); // Chamada da função para a estratégia Média
				break;
				
			case "TV":
				IJ.log("Executando Estratégia de TV");
				tv(imp); // Chamada da função para a estratégia TV
				break;
				
			case "Digital":
				IJ.log("Executando Estratégia Digital");
				digital(imp); // Chamada da função para a estratégia Digital
				break;
				
			default:
				IJ.log("Nenhuma estratégia válida selecionada");
				break;
		}
	}

	
	public void digital(ImagePlus imp) {    
		// Obter o processador de imagem RGB
		ImageProcessor colorProcessor = imp.getProcessor();
		
		int width = colorProcessor.getWidth();
		int height = colorProcessor.getHeight();
		
		// Criar um ByteProcessor para a imagem em escala de cinza
		ImageProcessor grayscaleProcessor = new ByteProcessor(width, height);
		
		double wR = 0.2125;  // Peso para o canal vermelho
		double wG = 0.7154;  // Peso para o canal verde
		double wB = 0.072;   // Peso para o canal azul
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int[] rgb = colorProcessor.getPixel(x, y, (int[]) null);
				int grayscale = (int) Math.round(wR * rgb[0] + wG * rgb[1] + wB * rgb[2]);
				
				// Colocar o valor de cinza no ByteProcessor
				grayscaleProcessor.putPixel(x, y, grayscale);
			}
		} 
		
		// Criar nova imagem em tons de cinza com o ByteProcessor
		ImagePlus grayscale = new ImagePlus("Grayscale Image Digital", grayscaleProcessor);	
		
		grayscale.show();
		IJ.save(grayscale, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 4 - Desenvolver um plugin para a conversão de uma Imagem RGB em Escala de Cinza/grayscale-digital.png");
	}
	
	
	
	public void tv(ImagePlus imp) {    
		// Obter o processador de imagem RGB
		ImageProcessor colorProcessor = imp.getProcessor();
		
		int width = colorProcessor.getWidth();
		int height = colorProcessor.getHeight();
		
		// Criar um ByteProcessor para a imagem em escala de cinza
		ImageProcessor grayscaleProcessor = new ByteProcessor(width, height);
		
		double wR = 0.299;  // Peso para o canal vermelho
		double wG = 0.587;  // Peso para o canal verde
		double wB = 0.114;   // Peso para o canal azul
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int[] rgb = colorProcessor.getPixel(x, y, (int[]) null);
				int grayscale = (int) Math.round(wR * rgb[0] + wG * rgb[1] + wB * rgb[2]);
				
				// Colocar o valor de cinza no ByteProcessor
				grayscaleProcessor.putPixel(x, y, grayscale);
			}
		} 
		
		// Criar nova imagem em tons de cinza com o ByteProcessor
		ImagePlus grayscale = new ImagePlus("Grayscale Image TV Analogica", grayscaleProcessor);	
		
		grayscale.show();
		IJ.save(grayscale, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 4 - Desenvolver um plugin para a conversão de uma Imagem RGB em Escala de Cinza/grayscale-tv-analogica.png");

	}
	
	
	
	public void media(ImagePlus imp) {    
		// Obter o processador de imagem RGB
		ImageProcessor colorProcessor = imp.getProcessor();
		
		int width = colorProcessor.getWidth();
		int height = colorProcessor.getHeight();
		
		// Criar um ByteProcessor para a imagem em escala de cinza
		ImageProcessor grayscaleProcessor = new ByteProcessor(width, height);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int[] rgb = colorProcessor.getPixel(x, y, (int[]) null);
				int grayscale = (int) Math.round((rgb[0] + rgb[1] + rgb[2])/3);
				
				// Colocar o valor de cinza no ByteProcessor
				grayscaleProcessor.putPixel(x, y, grayscale);
			}
		} 
		
		// Criar nova imagem em tons de cinza com o ByteProcessor
		ImagePlus grayscale = new ImagePlus("Grayscale Image Media", grayscaleProcessor);	
		
		grayscale.show();
		IJ.save(grayscale, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 4 - Desenvolver um plugin para a conversão de uma Imagem RGB em Escala de Cinza/grayscale-media.png");
	}

}
