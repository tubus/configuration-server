package com.github.tubus.ui.data.dto.param;

import com.github.tubus.ui.data.dto.component.Component;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentEnvironmentProfile {

    private Component component;

    private Environment environment;

    private EnvironmentProfile profile;
}