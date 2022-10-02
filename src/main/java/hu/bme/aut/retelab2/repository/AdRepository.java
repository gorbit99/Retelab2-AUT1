package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.AccessDeniedException;
import hu.bme.aut.retelab2.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class AdRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save(Ad ad) {
        ad.setSecretId(SecretGenerator.generate());
        return em.merge(ad);
    }

    public List<Ad> searchByPrice(int min, int max) {
        List<Ad> result = em.createQuery("select a from Ad a where a.price between ?1 and ?2", Ad.class)
                .setParameter(1, min)
                .setParameter(2, max)
                .getResultList();
        result.forEach(ad -> ad.setSecretId(null));
        return result;
    }

    @Transactional
    public Ad updateAd(Ad updated) throws AccessDeniedException {
        Ad found = em.find(Ad.class, updated.getId());

        if (!Objects.equals(found.getSecretId(), updated.getSecretId())) {
            throw new AccessDeniedException();
        }
        return save(updated);
    }

    public List<Ad> searchByTag(String tag) {
        List<Ad> result = em.createQuery("select a from Ad a where ?1 member a.tags", Ad.class)
                .setParameter(1, tag)
                .getResultList();
        result.forEach(ad -> ad.setSecretId(null));
        return result;
    }

    @Scheduled(fixedDelay = 6000)
    @Transactional
    @Modifying
    public void deleteOldEntries() {
        //https://hibernate.atlassian.net/browse/HHH-5529
        List<Ad> old = em.createQuery("select a from Ad a where a.expiryDate < ?1", Ad.class)
                .setParameter(1, LocalDateTime.now())
                .getResultList();

        old.forEach(ad -> {
            ad.getTags().clear();
            em.remove(ad);
        });
    }
}
