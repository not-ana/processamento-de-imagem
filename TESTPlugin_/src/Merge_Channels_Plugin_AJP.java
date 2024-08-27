import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;

public class Merge_Channels_Plugin_AJP implements PlugIn {

    public void run(String arg) {
    
    //1 PASSO
    	//mostrar as imagens que eu salvei rodando o plugin Split_Channels

    	ImagePlus red_channel = IJ.openImage("/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/red_channel.png");
        // Mostrar as imagens dos canais
        red_channel.show();
        
        ImagePlus green_channel = IJ.openImage("/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/green_channel.png");
        green_channel.show();
        
        ImagePlus blue_channel = IJ.openImage("/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/blue_channel.png");
        blue_channel.show();
        
        ImagePlus mergedImage = mergeChannels(red_channel, green_channel, blue_channel);
        
        mergedImage.show();
        
        saveChannel(mergedImage);
    }
    
	
    public ImagePlus mergeChannels(ImagePlus red_channel, ImagePlus green_channel, ImagePlus blue_channel) {   
        
        if (red_channel == null || green_channel == null || blue_channel == null) {
            IJ.error("Erro ao abrir a imagem");
            return null;
        } 
       //estou considerando que se eu consegui pegar a imagem salva, ela passou pelo splitchannel. que ja conferiu que ela é RGB


        //2 PASSO
        // Obter processadores de imagem
        ImageProcessor red_processor = red_channel.getProcessor();
        ImageProcessor green_processor = green_channel.getProcessor();
        ImageProcessor blue_processor = blue_channel.getProcessor();

        int width = red_processor.getWidth();
        int height = red_processor.getHeight();

    	
        //3 PASSO
        // Verificar se todas as imagens têm o mesmo tamanho
        if (green_processor.getWidth() != width || green_processor.getHeight() != height ||
        		blue_processor.getWidth() != width || blue_processor.getHeight() != height) {
            IJ.error("Os canais têm tamanhos diferentes.");
            return null;
        }
            
        
        //4 PASSO      
        // Criar um processador de imagem RGB
    	//fazer o merge dos 3 canais em 1 imagem
            
        ColorProcessor colorProcessor = new ColorProcessor(width, height);

        // Iterar sobre cada pixel para combinar os canais
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = red_processor.getPixel(x, y);
                int green = green_processor.getPixel(x, y);
                int blue = blue_processor.getPixel(x, y);

                // Combinar os canais em um valor RGB
                int rgb = (red << 16) | (green << 8) | blue;

                // Definir o pixel no processador de imagem RGB
                colorProcessor.putPixel(x, y, rgb);
            }
        }    


        // Criar uma nova imagem RGB
        ImagePlus mergedImage = new ImagePlus("Merged Image", colorProcessor);
        return mergedImage;        
    }
    
    
    private void saveChannel(ImagePlus mergedImage) {    	 
        // Salvar os canais em arquivos
        IJ.save(mergedImage, "/home/ana/IFF 2024.1/3 TERÇA - Processam. Imagem (Fabio)/Trab 2 - Desenvolvimento de dois plugins para manipular canais de uma imagem RGB/merged_image.png");
    }
}