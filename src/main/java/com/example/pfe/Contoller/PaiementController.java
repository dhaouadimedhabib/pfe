package com.example.pfe.Contoller;

import com.example.pfe.Service.PaiementService;
import com.example.pfe.payload.request.PaymentRequest;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/paiement")
public class PaiementController {
    @Autowired
    private PaiementService paiementService;

    @PostMapping("/payer")
    public ResponseEntity<?> payerRendezVous(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Utilisez les valeurs de paymentRequest
            PaymentIntent paymentIntent = paiementService.createPaymentIntent(paymentRequest.getToken(),
                    paymentRequest.getCurrency(),
                    paymentRequest.getIdRendezVous());

            // Convertir PaymentIntent en Map pour éviter les problèmes de sérialisation
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("id", paymentIntent.getId());
            responseMap.put("amount", paymentIntent.getAmount());
            responseMap.put("currency", paymentIntent.getCurrency());
            responseMap.put("status", paymentIntent.getStatus());
            responseMap.put("description", paymentIntent.getDescription());

            // Utiliser les détails du PaymentMethod
            PaymentMethod paymentMethodDetails = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());
            PaymentMethod.Card card = paymentMethodDetails.getCard();
            String last4Digits = card.getLast4();

            responseMap.put("card_last4", last4Digits);

            return ResponseEntity.ok(responseMap);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de paiement : " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rendez-vous non trouvé : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur interne : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/montant")
    public ResponseEntity<BigDecimal> getMontant(@PathVariable Long id) {
        try {
            BigDecimal montant = paiementService.getMontantForRendezVous(id);
            return ResponseEntity.ok(montant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
