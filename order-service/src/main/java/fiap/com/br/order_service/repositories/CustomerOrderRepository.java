package fiap.com.br.order_service.repositories;

import fiap.com.br.order_service.models.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {}
