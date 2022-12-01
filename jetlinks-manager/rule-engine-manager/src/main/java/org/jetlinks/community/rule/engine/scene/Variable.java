package org.jetlinks.community.rule.engine.scene;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hswebframework.ezorm.core.param.Term;
import org.jetlinks.core.metadata.types.StringType;
import org.jetlinks.community.rule.engine.scene.term.TermType;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Variable {
    public static final String OPTION_PRODUCT_ID = "productId";
    @Schema(description = "变量ID")
    private String id;

    @Schema(description = "变量名")
    private String name;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "类型")
    private String type;

    /**
     * @see Term#getTermType()
     */
    @Schema(description = "支持的条件类型")
    private List<TermType> termTypes;

    @Schema(description = "子级变量")
    private List<Variable> children;

    @Schema(description = "其他配置")
    private Map<String, Object> options;

    public synchronized Map<String, Object> safeOptions() {
        return options == null ? options = new HashMap<>() : options;
    }

    public Variable withOption(String key, Object value) {
        safeOptions().put(key, value);
        return this;
    }

    public Variable withOptions(Map<String, Object> options) {
        safeOptions().putAll(options);
        return this;
    }

    public Variable withType(String type) {
        this.type = type;
        return this;
    }

    public void refactorPrefix() {
        if (CollectionUtils.isNotEmpty(children)) {
            for (Variable child : children) {
                if (!child.getId().startsWith(id + ".")) {
                    child.setId(id + "." + child.getId());
                }

                if (StringUtils.hasText(child.description)
                    && StringUtils.hasText(description)) {
                    child.setDescription(description + "/" + child.description);
                }
                child.refactorPrefix();
            }
        }
    }

    private Variable(String id, String name) {
        this.id = id;
        this.name = name;
        this.description = name;
        this.type = StringType.ID;
    }

    public Variable() {
    }

    public static Variable of(String id, String name) {
        return new Variable(id, name);
    }

}
