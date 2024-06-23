package gooroommoon.algofi_core.algorithmproblem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlgorithmproblemRepository extends JpaRepository<Algorithmproblem, Long> {

    List<Algorithmproblem> findAllByLevel(String level);
}
