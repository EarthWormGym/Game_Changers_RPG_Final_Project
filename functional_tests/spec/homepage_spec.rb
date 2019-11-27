require "spec_helper"
feature "homepage" do
  scenario "I should be able to visit the homepage" do
    visit '/'
    expect(page).to have_content 'Welcome to Makers and Mortals, Prepare for carnage'
  end
  scenario "I should be able to visit the homepage" do
      visit '/home'
      expect(page).to have_content 'Welcome to Makers and Mortals, Prepare for carnage'
   end
end
