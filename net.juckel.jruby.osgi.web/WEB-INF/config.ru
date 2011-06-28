$LOAD_PATH << "classpath:/WEB-INF"
require 'sample_app'

app = SampleApp.new 'services'
map '/services' do
  run app
end
