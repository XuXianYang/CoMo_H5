(function($) {
  $.fn.btnSelector = function() {
    this.each(init);

    function init() {
      var $container = $(this);
      var type = $container.data('type') || 'radio';
      var value = $container.data('value');

      if (type === 'radio') {
        $container.find('.btn-selector').each(function() {
          var $this = $(this);
          if ($this.data('value') === value) {
            $this.addClass('selected');
          } else {
            $this.removeClass('selected');
          }
        });
      } else {
        // checkbox
        var values = value;
        if (typeof value === 'string') {
          values = value.split(',');
        }
        $container.find('.btn-selector').each(function() {
          var $this = $(this);
          if ($.inArray($this.data('value'), values) >= 0) {
            $this.addClass('selected');
          } else {
            $this.removeClass('selected');
          }
        })
      }

      // bind event
      $container.on('click', '.btn-selector', function() {
        var oldValue = $container.data('value');
        var newValue = null;
        var $this = $(this);
        if (type === 'radio') {
          if ($this.is('.selected')) {
            return;
          }
          $container.find('.btn-selector').removeClass('selected');
          $this.addClass('selected');
          newValue = $this.data('value');
        } else {
          $this.toggleClass('selected');

          // checkbox
          newValue = [];
          $container.find('.btn-selector.selected')
            .each(function() {
              newValue.push($(this).data('value'));
            });
        }

        $container.data('value', newValue);
        $container.trigger('valueChanged', {
          oldValue: oldValue,
          newValue: newValue
        })
      })
    }
		return this;
  }
})(jQuery);
