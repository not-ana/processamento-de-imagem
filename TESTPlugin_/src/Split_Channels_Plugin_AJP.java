import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ByteProcessor;

public class Split_Channels_Plugin_AJP implements PlugIn {

    public void run(String arg) {
        ImagePlus imp = IJ.openImage();
        
        if (imp == null) {
            IJ.error("Erro ao abrir a imagem");
            return;
        } 
        else if (imp.getType() != ImagePlus.COLOR_RGB) {
        	IJ.error("Apenas imagens RGB são suportadas!");
        }

        ImagePlus[] channels = splitChannels(imp);

        channels[0].show();  // Red 
        channels[1].show();  // Green 
        channels[2].show();  // Blue 
        
        saveChannels(channels[0], channels[1], channels[2]);  
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

        
    private void saveChannels(ImagePlus red_channel, ImagePlus green_channel, ImagePlus blue_channel) {    	 
        IJ.save(red_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/red_channel.png");
        IJ.save(green_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/green_channel.png");
        IJ.save(blue_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/blue_channel.png"); 
    }
 }