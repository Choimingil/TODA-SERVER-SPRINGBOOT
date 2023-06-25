package com.toda.api.TODASERVERSPRINGBOOT.utils.extenders;

import jakarta.servlet.http.HttpServletRequest;
import net.gpedro.integrations.slack.SlackField;

public interface SlackKeysExtender {
    SlackField setFieldWithRequest(HttpServletRequest request);
    SlackField setFieldWithMdc();
}
