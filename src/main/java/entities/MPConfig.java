package entities;

import com.mercadopago.MercadoPagoConfig;

public class MPConfig {
    
    private static final String ACCESS_TOKEN = "APP_USR-823938148084228-112018-cd6d4b64190341ff5f31cc38c0b4312d-660480912"; 
    
    public static void initialize() {
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
    }
}