package ifba.multimetro.util;

/**
 * Created by brenno on 08/08/16.
 */
public class ConversaoLeituras {

    private static String escalaLeitura;

    public static float converterLeitura(float leitura, String tipo){
        switch (tipo){
            case "T":
                if(leitura<0){
                    if(leitura>-1){
                        leitura *= 1000;
                        setEscalaLeitura("mV");
                    }else
                        setEscalaLeitura("V");
                }else if(leitura<1){
                    leitura *= 1000;
                    setEscalaLeitura("mV");
                }else
                    setEscalaLeitura("V");

            break;
            case "C":
                if(leitura<0) {
                    if (leitura>-1) {
                        leitura *= 1000;
                        setEscalaLeitura("mA");
                    } else
                        setEscalaLeitura("A");
                }else if(leitura<1){
                        leitura *= 1000;
                        setEscalaLeitura("mA");
                }else
                        setEscalaLeitura("A");
            break;
            case "R":
                if(leitura>=1000){
                    leitura /= 1000;
                    setEscalaLeitura("KΩ");
                }else
                    setEscalaLeitura("Ω");
            break;
        }

        return leitura;
    }

    public static String getEscalaLeitura() {
        return escalaLeitura;
    }

    public static void setEscalaLeitura(String escalaLeitura) {
        ConversaoLeituras.escalaLeitura = escalaLeitura;
    }
}
