package org.bitvector.microservice_test;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;

@Accessor
public interface ProductAccessor {

    @Query("SELECT * FROM microservice_test.products")
    ListenableFuture<Result<Product>> getAllAsync();

}
