package xyz.erupt.cloud.server.model;

import lombok.Getter;
import lombok.Setter;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.jpa.model.MetaModel;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author YuePeng
 * date 2021/12/16 00:32
 */
@Getter
@Setter
@Entity
@Table(name = "e_cloud_node_group", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Erupt(name = "服务组别")
public class CloudNodeGroup extends MetaModel {

    @EruptField(
            views = @View(title = "编码", sortable = true),
            edit = @Edit(title = "编码", notNull = true, search = @Search(vague = true))
    )
    private String code;

    @EruptField(
            views = @View(title = "名称", sortable = true),
            edit = @Edit(title = "名称", notNull = true, search = @Search(vague = true))
    )
    private String name;

    @Lob
    @EruptField(
            views = @View(title = "描述"),
            edit = @Edit(title = "描述",type = EditType.HTML_EDITOR, search = @Search(vague = true))
    )
    private String remark;

}
