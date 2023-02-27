/**
 * @license Copyright (c) 2003-2022, CKSource Holding sp. z o.o. All rights reserved.
 * For licensing, see https://ckeditor.com/legal/ckeditor-oss-license
 */

CKEDITOR.editorConfig = function( config ) {
    config.allowedContent = true;
    config.toolbar = 'Full';
    config.extraPlugins = ['lightSave','normalSave','heavySave'];
    config.toolbar_Full =
        [
            { name: 'document', items : [ 'Source','-','Save','DocProps','Preview','Print','-','Templates' ] },
            { name: 'editing', items : [ 'Find','-','ShowBlocks'] },
            { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript'] },
            { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Blockquote',
                    '-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
            { name: 'links', items : [ 'Link','Unlink'] },
            { name: 'insert', items : [ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar'] },
            '/',
            { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
            { name: 'tools', items : [ 'LightSave','NormalSave','HeavySave'] }
        ];

    // config.toolbar_Basic =
    //     [
    //         ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink','-','About']
    //     ];
};
