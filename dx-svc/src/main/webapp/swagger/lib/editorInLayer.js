var promptLayer = function(){

    var layerId;
    var editor = {};
    var resultDOM = {};
    var exampleJson = {};
    var underEditJson = {};
    
    var jsonToTree = function() {
        var jsonObj = textToObj(editor.getValue(),null);
        if(jsonObj){
            setJsonTreeEditor(jsonObj);
            disableLayerErrorMsg();
        }else{
            setLayerErrorMsg('json 格式错误');
        }
        
    };
    
    
    
    
    
    var initPopUp = function(){
    $('input[jsonId]').click(function(){
        // layer.prompt({
        //     title: '输入任何口令，并确认',
        //     formType: 1 //prompt风格，支持0-2
        //     }, function(pass){
        //     layer.prompt({title: '随便写点啥，并确认', formType: 2}, function(text){
        //         layer.msg('演示完毕！您的口令：'+ pass +' 您最后写下了：'+ text);
        //     });
        // });    
        //try to parse json data
        
        if($(this).prev().val().trim()){
            underEditJson = textToObj($(this).prev().val(), null);
        }else{
            underEditJson = textToObj($(this).prev().val(), swaggerUi.jsonIdMap[$(this).attr('jsonId')].value);
        }
        exampleJson = textToObj(swaggerUi.jsonIdMap[$(this).attr('jsonId')].value);
        //set jsonObj title
        $('#jsonMateEditor').find('#jsonMateName').html(swaggerUi.jsonIdMap[$(this).attr('jsonId')].name);
        
        resultDOM = $(this).prev();
        
        //initial json Tree Editor 
        setJsonTreeEditor(underEditJson);
        
        //initial ace editor for json
        editor = ace.edit("aceEditor");
        editor.setTheme("ace/theme/crimson_editor");
        editor.getSession().setMode("ace/mode/javascript");

        //$('#jsonMateResult').val(JSON.stringify(underEditJson)); //as a textarea
        
        //finish data prepare, show div in layer
        layerId = layer.open({
            type: 1,
            title: ['修改JSON',';font-size:18px;'],
            closeBtn: 1,
            //shadeClose: true,
            skin: 'layui-layer-molv',
            area: ['1200px', '530px'],
            maxmin: true,
            fix: false,
            content: $('#hideEditorArea'),
            btn: ['改好了', '不改了'],
            //yes: function(){alert('only alert close');}//getEditorResultToPage
        });
        if(underEditJson){
            editor.setValue(JSON.stringify(underEditJson, null, 4));
        }else{
            editor.setValue($(this).prev().val());
            jsonToTree();
        }
        $('.layui-layer-btn0').click(getEditorResultToPage);
        editor.on('blur', jsonToTree );
    });
    };
    
    var treeToJsonText = function(data){
        editor.setValue(JSON.stringify(data, null, 4));
    };
    
    
    
    var textToObj = function(text, alternateText){
        var result = {};        
        try{
            if(text){
                result = JSON.parse(text);
            }else{
                result = JSON.parse(alternateText);
            }
                
        }catch(_error){
            console.log('json识别出错,错误信息如下');
            console.log(_error);
            console.log('输入的json如下');
            console.log(text);
            result = JSON.parse(alternateText);
        }
        return result;
    };
    
    var getEditorResultToPage = function(index, layero){
        //alert('close');
        //右侧文本框的值无法完成JSON.parse时，出现提示框，但会继续关闭弹层并赋值
        var result1 = textToObj(editor.getValue(),null);
        if(!result1){
            layer.msg('请注意，编辑完成的json文本有格式错误');
        }
        //layer.msg('注意，json文本有格式错误，请确认');
        
        resultDOM.val(editor.getValue());
        layer.close(index);
        editor.destroy();
        $('#aceEditor').html('');
    };
    
    var setJsonTreeEditor = function(jsonObj){
        var opt = {
            change: treeToJsonText,
            propertyclick: function (path) {
                 console.log(path);/* called when a property is clicked with the JS path to that property */ 
                },
            onValueClick: jsonEditorValueClicked
        };
        /* opt.propertyElement = '<textarea>'; */ // element of the property field, <input> is default
        /* opt.valueElement = '<textarea>'; */  // element of the value field, <input> is default
        
        $('#jsonMateEditor').jsonEditor(jsonObj, opt);
        
    };
    
    var setLayerErrorMsg = function(msg){
        if($('#layerErrMsg')[0]){
            $('#layerErrMsg').html(msg); 
        }else{
            $('.layui-layer-btn').prepend('<div id="layerErrMsg" style="float:left; color:red">' + msg + '</div>');    
        }
    };
    
    var disableLayerErrorMsg = function(){
        if($('#layerErrMsg')[0]){
            $('#layerErrMsg').html(''); 
        }
    };
    
    
    var jsonEditorValueClicked = function(path, value, $element){
        // console.log(JSON.stringify(exampleJson));
        // console.log(path); console.log(value);
        if($($element.next()[0]).prop("tagName") === "SELECT")
        {
            setLayerErrorMsg("请从右侧下拉框选择数据");
            return;
        }

        
        var pathInfo = JSON.parse(path);
        var pathValue = {};
        for(var x = 0; x < pathInfo.length; x++){
            if(pathValue !== undefined){
                pathValue = exampleJson[pathInfo[x]];
            }else{
                return;
            }
        }
        if(pathValue.indexOf && pathValue.indexOf('enum:[') == 0 ){
            var enumArray = JSON.parse(pathValue.substring(5));
            $select = buildSelectFromArray(enumArray);
            
            
            $element.after($select);
        }
        
    };
    
    var buildSelectFromArray = function(array){
        var $select = $('<select></select>');
        for(var x = 0; x < array.length; x++){
            var $option = $('<option value="'+ array[x] +'">'+ array[x] +'</option>');
            $select.append($option); 
        };
        $select.change(function(){ 
            //alert($(this).children('option:selected').val()); 
            
            $(this).prev().val('"' + $(this).children('option:selected').val()+ '"');
            $(this).prev().change();
            disableLayerErrorMsg();
        });
        return $select;
        
    };
    
    return {
        exampleJson : function(){return exampleJson;},
        initPopUp : initPopUp 
    }
}();

