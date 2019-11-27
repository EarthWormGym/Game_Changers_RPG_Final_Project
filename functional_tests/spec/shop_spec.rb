require "spec_helper"
feature "shop" do
  scenario "I should be able to visit the shop page" do
    visit '/shop'
    expect(page).to have_content 'Welcome to the Shop'
    expect(page).to have_content 'What would you like to increase?'
  end
end
