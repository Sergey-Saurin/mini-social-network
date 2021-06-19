package network.repository.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Sizenko Egor on 01.04.2021.
 */

@Getter
@Setter
@AllArgsConstructor

public class SearchCriteria {
	private String key;
	private String operation;
	private Object value;
}
