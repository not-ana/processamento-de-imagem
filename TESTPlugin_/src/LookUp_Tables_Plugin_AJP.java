import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ByteProcessor;
import ij.process.LUT;

public class LookUp_Tables_Plugin_AJP implements PlugIn {

    public void run(String arg) {
    	
        ImagePlus imp = IJ.openImage();
        
        ImagePlus[] channels = splitChannels(imp);
        
        applyLookUpTables(channels[0].getProcessor(), channels[1].getProcessor(), channels[2].getProcessor());
        
        saveChannels(channels[0], channels[1], channels[2]);    
    }

    
    public ImagePlus[] splitChannels(ImagePlus imp) {
    	
        if (imp == null) {
            IJ.error("Erro ao abrir a imagem");
            return null;
        } 
        else if (imp.getType() != ImagePlus.COLOR_RGB) {
            IJ.error("Apenas imagens RGB são suportadas!");
            return null;
        }

        // Obter o processador de imagem RGB
        ImageProcessor colorProcessor = imp.getProcessor();

        int width = colorProcessor.getWidth();
        int height = colorProcessor.getHeight();
        
        // Criar processadores de imagem para os canais
        ImageProcessor redProcessor = new ByteProcessor(width, height);
        ImageProcessor greenProcessor = new ByteProcessor(width, height);
        ImageProcessor blueProcessor = new ByteProcessor(width, height);

        int[] canais = new int[3];
        
        // Iterar sobre cada pixel para separar os canais
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Obter os valores RGB para o pixel (x, y)
                colorProcessor.getPixel(x, y, canais);

                // Atribuir os valores aos processadores correspondentes
                redProcessor.putPixel(x, y, canais[0]); 
                greenProcessor.putPixel(x, y, canais[1]);  
                blueProcessor.putPixel(x, y, canais[2]);  
            }
        }   
        
        // Criar imagens para cada canal
        ImagePlus red_channel = new ImagePlus("Red Channel", redProcessor);
        ImagePlus green_channel = new ImagePlus("Green Channel", greenProcessor);
        ImagePlus blue_channel = new ImagePlus("Blue Channel", blueProcessor);
        
        return new ImagePlus[] {red_channel, green_channel, blue_channel};       
    }
    
    
    public void applyLookUpTables(ImageProcessor redProcessor, ImageProcessor greenProcessor, ImageProcessor blueProcessor) {
    	
        // Criar LUTs
        LUT redLUT = createRedLUT();
        LUT greenLUT = createGreenLUT();
        LUT blueLUT = createBlueLUT();

        // Aplicar LUTs aos processadores de imagem
        redProcessor.setLut(redLUT);
        greenProcessor.setLut(greenLUT);
        blueProcessor.setLut(blueLUT);
        
        // Criar imagens para cada canal
        ImagePlus red_channel = new ImagePlus("Red Channel", redProcessor);
        ImagePlus green_channel = new ImagePlus("Green Channel", greenProcessor);
        ImagePlus blue_channel = new ImagePlus("Blue Channel", blueProcessor);
        
        // Mostrar as imagens dos canais
        red_channel.show();
        green_channel.show();
        blue_channel.show();
        
        
        createRedLUT();
        createGreenLUT();
        createBlueLUT();
        
        saveChannels(red_channel, green_channel, blue_channel);
    	
    }
       
    
    private LUT createRedLUT() {
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        
        for (int i = 0; i < 256; i++) {
            red[i] = (byte) i;
            green[i] = 0;
            blue[i] = 0;
        }
        return new LUT(red, green, blue);
    }
    
    private LUT createGreenLUT() {
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        
        for (int i = 0; i < 256; i++) {
            red[i] = 0;
            green[i] = (byte) i;
            blue[i] = 0;
        }
        
        return new LUT(red, green, blue);
    }

    private LUT createBlueLUT() {
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        
        for (int i = 0; i < 256; i++) {
            red[i] = 0;
            green[i] = 0;
            blue[i] = (byte) i;
        }
        
        return new LUT(red, green, blue);
    }

    private void saveChannels(ImagePlus red_channel, ImagePlus green_channel, ImagePlus blue_channel) {    	 
        // Salvar os canais em arquivos
        IJ.save(red_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 3 - Alterar o plugin anterior para trabalhar com Lookup Tables/red_channel.png");
        IJ.save(green_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 3 - Alterar o plugin anterior para trabalhar com Lookup Tables/green_channel.png");
        IJ.save(blue_channel, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 3 - Alterar o plugin anterior para trabalhar com Lookup Tables/blue_channel.png"); 
    }


}
