$LOAD_PATH << "classpath:/WEB-INF"
puts "LOAD_PATH: #{$LOAD_PATH.join ","}"
require 'sample_app'

app = SampleApp.new
map '/foo' do
  run app
end
