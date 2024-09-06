package com.example.pfe.Service;

import com.example.pfe.Domain.*;
import com.example.pfe.Repo.PaiementRepo;
import com.example.pfe.Repo.ProfessionnelRepo;
import com.example.pfe.Repo.RendezVousRepo;
import com.example.pfe.Repo.ServiceRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.stripe.model.Charge;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class PaiementService {
    @Autowired
    RendezVousRepo rendezVousRepository;

    @Autowired
    private PaiementRepo paiementRepository;
    @Autowired
    ProfessionnelRepo professionnelRepo;
    @Autowired
    ServiceRepo serviceRepo;
    private static final Logger log = LoggerFactory.getLogger(PaiementService.class);

    public PaymentIntent createPaymentIntent(String token, String currency, Long idRendezVous) throws StripeException {
        log.info("Token: {}, Currency: {}, idRendezVous: {}", token, currency, idRendezVous);

        RendezVous rendezVous = rendezVousRepository.findById(idRendezVous)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        RendezVous rendez = rendezVousRepository.findById(idRendezVous).get();
        Professionnel professionnel = professionnelRepo.findByRendezVous(rendez);
        Services service = serviceRepo.findByProfessionnel(professionnel);

        BigDecimal montant = service.getPrix();
        log.info("Montant: {}", montant);

        // Créer un client (si nécessaire, sinon utilisez un client existant)
        Customer customer = Customer.create(new HashMap<>());

        // Créer la méthode de paiement (PaymentMethod) à partir du token
        PaymentMethod paymentMethod = PaymentMethod.create(
                Map.of(
                        "type", "card",
                        "card", Map.of("token", token)
                )
        );

        // Attacher la méthode de paiement au client
        paymentMethod.attach(Map.of("customer", customer.getId()));

        // Créer un Payment Intent
        Map<String, Object> paymentIntentParams = new HashMap<>();
        paymentIntentParams.put("amount", montant.longValue() * 100); // Montant en centimes
        paymentIntentParams.put("currency", currency);
        paymentIntentParams.put("customer", customer.getId());
        paymentIntentParams.put("payment_method", paymentMethod.getId());
        paymentIntentParams.put("confirm", true); // Confirmer le paiement immédiatement

        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentParams);

        // Récupérer les détails de la méthode de paiement
        PaymentMethod paymentMethodDetails = PaymentMethod.retrieve(paymentIntent.getPaymentMethod());
        PaymentMethod.Card card = paymentMethodDetails.getCard();
        String last4Digits = card.getLast4();

        // Créer un objet Paiement et le lier au RendezVous
        Paiement paiement = new Paiement();
        paiement.setMontant(montant);
        paiement.setCurrency(paymentIntent.getCurrency());
        paiement.setMethodePaiement("CARTE");
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setRendezVous(rendezVous);
        paiement.setCardNumber(last4Digits);

        // Enregistrer le paiement
        paiementRepository.save(paiement);

        // Mettre à jour le statut du RendezVous
        rendezVous.setEtat("Paye");
        rendezVousRepository.save(rendezVous);

        return paymentIntent;
    }
    public BigDecimal getMontantForRendezVous(Long idRendezVous) {
        RendezVous rendezVous = rendezVousRepository.findById(idRendezVous)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        Professionnel professionnel = professionnelRepo.findByRendezVous(rendezVous);
        Services service = serviceRepo.findByProfessionnel(professionnel);

        return service.getPrix(); // Assurez-vous que getPrix() retourne un BigDecimal
    }
}
