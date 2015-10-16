module Jekyll
  class ReleasePostCreater < Generator
    safe false

    def replace(filepath, regexp, *args, &block)
      content = File.read(filepath).gsub(regexp, *args, &block)
      File.open(filepath, 'wb') { |file| file.write(content) }
    end

    def generate(site)
      puts "ReleasePostCreater started..."
      @files = Dir["_publish/*"]
      @files.each_with_index { |f,i|
        puts "Found file #{i}"
        now = DateTime.now.strftime("%Y-%m-%d %H:%M:%S")
        version = File.basename(f)
        replace(f, /^date: unpublished/mi) { |match| "date:  " + now + "" }
        replace(f, /VERSION/m) { |match| version }
        now = Date.today.strftime("%Y-%m-%d")
        newName = "_posts/#{now}-v#{version}-released.markdown"
        File.rename(f, newName)
        puts "Created #{newName}"
      }
    end
  end
end
