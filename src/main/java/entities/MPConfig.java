package entities;

import com.mercadopago.MercadoPagoConfig;

public class MPConfig {
    
    private static final String ACCESS_TOKEN = "TEST-823938148084228-112018-f842aba74684673c394867dc4ef8f1bf-660480912"; 
    
    public static void initialize() {
        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
    }
}