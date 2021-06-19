package network.repository.util;

import network.model.entity.Person;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sizenko Egor on 01.04.2021.
 */

public class PersonSpecificationsBuilder {
	private final List<SearchCriteria> params;

	public PersonSpecificationsBuilder() {
		params = new ArrayList<>();
	}

	public PersonSpecificationsBuilder with(String key, String operator, Object value) {
		params.add(new SearchCriteria(key, operator, value));
		return this;
	}

	public Specification<Person> build() {
		if (params.size() == 0) {
			return null;
		}

		List<Specification> specs = params.stream()
				.map(PersonSpecification::new)
				.collect(Collectors.toList());

		Specification result = specs.get(0);

		for (int i = 1; i < params.size(); i++) {
			result = Specification.where(result).and(specs.get(i));
		}
		return result;
	}
}
