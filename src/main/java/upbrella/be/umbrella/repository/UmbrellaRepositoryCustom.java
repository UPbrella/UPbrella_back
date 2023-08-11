package upbrella.be.umbrella.repository;

public interface UmbrellaRepositoryCustom {

    long countAllUmbrellas();

    long countRentableUmbrellas();

    long countRentedUmbrellas();

    long countMissingUmbrellas();

    long countRentableUmbrellasByStore(long storeMetaId);

    long countRentedUmbrellasByStore(long storeMetaId);

    long countAllUmbrellasByStore(long storeId);

    long countMissingUmbrellasByStore(long storeId);
}
