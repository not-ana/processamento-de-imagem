import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import java.awt.AWTEvent;


// Criar um plugin para alterar os valores de brilho e contraste de uma imagem,
//bem como aplicar as técnicas de solarização e dessaturação na  mesma.



public class Operacoes_Ponto_a_Ponto implements PlugIn, DialogListener {
	
	private ImagePlus imp;
	private ImagePlus originalImp;
	
    public void run(String arg) { 

    	imp = IJ.openImage();
        
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
		
		imp.show();
		
		//duplico a imagem original por segurança, caso queira cancelar alterações
		originalImp = imp.duplicate();
   
    }
    
    
    
    
    
	public void apresentarInterfaceGrafica(ImagePlus imp) {
		GenericDialog interfaceGrafica = new GenericDialog("Operações Ponto a Ponto");
		interfaceGrafica.addDialogListener(this);
		
		interfaceGrafica.addMessage("Utilize os sliders abaixo para alterar o brilho, contraste, solarização e dessaturação da imagem.");
		interfaceGrafica.addSlider("Brilho", -255, 255, 0, 1);
		interfaceGrafica.addSlider("Contraste", 0.5, 3.0, 1.0, 0.01);
		interfaceGrafica.addSlider("Solarização", 0, 255, 128, 1);
		interfaceGrafica.addSlider("Dessaturação", 0, 100, 0, 1);

		interfaceGrafica.showDialog();
		

		if (interfaceGrafica.wasCanceled()) {			
            imp.setProcessor(originalImp.getProcessor());
			IJ.showMessage("Plugin cancelado!");
		} 
		else if (interfaceGrafica.wasOKed()) {				        
	        IJ.showMessage("Plugin encerrado com sucesso!");
		}
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog interfaceGrafica, AWTEvent e) {
		
		if (interfaceGrafica.wasCanceled()) return false;
		
        // Obter valores dos sliders
        int brilho = (int) interfaceGrafica.getNextNumber();
        double contraste = interfaceGrafica.getNextNumber();
        int solarizacao = (int) interfaceGrafica.getNextNumber();
        int dessaturacao = (int) interfaceGrafica.getNextNumber();
        
        // Aplicar as alterações na imagem
        ajustarBrilho(brilho);
        ajustarContraste(contraste, imp);
        aplicarSolarizacao(solarizacao);
        aplicarDessaturacao(dessaturacao);
        
        imp.updateAndDraw();
        imp.show();
        
		return true;
    }


    private void ajustarBrilho(int brilho) {
        imp.getProcessor().setMinAndMax(128 - brilho, 128 + brilho);
    }
    
    private void ajustarContraste(double contraste, ImagePlus imp) {
    	
    	//calcular fator de contraste F
    	//determinado pelo nivel de contraste C --> que pelo que entendi é o input do user   	
    	double f = (259 * (contraste + 255)) / (255 * (259 - contraste));
    	
    	
        // Obter o processador de cores da imagem
        ColorProcessor cp = (ColorProcessor) imp.getProcessor();
        
        // Iterar sobre todos os pixels da imagem
        for (int y = 0; y < imp.getHeight(); y++) {
            for (int x = 0; x < imp.getWidth(); x++) {
                // Obter o valor RGB do pixel
                int[] rgb = cp.getPixel(x, y, null);
                
                // Ajustar o contraste para cada canal de cor
                for (int i = 0; i < 3; i++) {
                    // Aplicar a fórmula de ajuste de contraste
                    rgb[i] = (int)(f * (rgb[i] - 128) + 128);
                    
                    // Garantir que o valor esteja dentro do intervalo [0, 255]
                    rgb[i] = Math.max(0, Math.min(255, rgb[i]));
                }
                
                // Definir o novo valor RGB do pixel
                cp.putPixel(x, y, rgb);
            }
        }
    }
    

    private void aplicarSolarizacao(int limiar) {
        // Obter o processador de cores da imagem
        ColorProcessor cp = (ColorProcessor) imp.getProcessor();
        
        // Iterar sobre todos os pixels da imagem
        for (int y = 0; y < imp.getHeight(); y++) {
            for (int x = 0; x < imp.getWidth(); x++) {
                // Obter o valor RGB do pixel
                int[] rgb = cp.getPixel(x, y, null);
                
                // Aplicar solarização invertendo os valores acima do limiar
                for (int i = 0; i < 3; i++) {
                    if (rgb[i] > limiar) {
                        rgb[i] = 255 - rgb[i]; // Inverter o valor do pixel
                    }
                }
                
                // Definir o novo valor RGB do pixel
                cp.putPixel(x, y, rgb);
            }
        }
        
        imp.updateAndDraw();
    }


    private void aplicarDessaturacao(int fator) {
        // Obter o processador de cores da imagem
        ColorProcessor cp = (ColorProcessor) imp.getProcessor();
        
        // Iterar sobre todos os pixels da imagem
        for (int y = 0; y < imp.getHeight(); y++) {
            for (int x = 0; x < imp.getWidth(); x++) {
                // Obter o valor RGB do pixel
                int[] rgb = cp.getPixel(x, y, null);
                
                // Calcular a média (tons de cinza) dos valores RGB
                int media = (rgb[0] + rgb[1] + rgb[2]) / 3;
                
                // Aplicar a dessaturação misturando o valor original com o tom de cinza
                for (int i = 0; i < 3; i++) {
                    rgb[i] = (rgb[i] * (100 - fator) + media * fator) / 100;
                }
                
                // Definir o novo valor RGB do pixel
                cp.putPixel(x, y, rgb);
            }
        }
        
        // Atualizar a imagem no ImageJ
        imp.updateAndDraw();
    }
    
    
    
    
    
    public ImagePlus[] splitChannels(ImagePlus imp) {
    			
    	// Obter o processador de imagem RGB
        ImageProcessor colorProcessor = imp.getProcessor();

        int width = colorProcessor.getWidth();
        int height = colorProcessor.getHeight();
        
        
        // Criar processadores de imagem para os canais
        //O byte processor vai ser usado pq a imagem é 8 bits. 
		        //ByteProcessor: Para imagens em tons de cinza (8 bits).
		        //ShortProcessor: Para imagens de 16 bits.
		        //FloatProcessor: Para imagens de 32 bits.
        //ColorProcessor: Para imagens RGB.
        //Depois pra voltar pro merge a gente usa o colorprocessor 
        ImageProcessor red_processor = new ByteProcessor(width, height);
        ImageProcessor green_processor = new ByteProcessor(width, height);
        ImageProcessor blue_processor = new ByteProcessor(width, height);
        
        
        int[] canais = {0,0,0};
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Obter os valores RGB para o pixel (x, y)
                colorProcessor.getPixel(x, y, canais);

                // Atribuir os valores aos processadores correspondentes
                red_processor.putPixel(x, y, canais[0]); 
                green_processor.putPixel(x, y, canais[1]);  
                blue_processor.putPixel(x, y, canais[2]);  
            }
        }
        
        // Criar imagens para cada canal
        ImagePlus red_channel = new ImagePlus("Red Channel", red_processor);
        ImagePlus green_channel = new ImagePlus("Green Channel", green_processor);
        ImagePlus blue_channel= new ImagePlus("Blue Channel", blue_processor);

        return new ImagePlus[] {red_channel, green_channel, blue_channel};        
    } 

}